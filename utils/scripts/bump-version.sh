# Get the new version from package.json using jq
new_version=$(cat package.json | jq -r '.version' )

# Update the mod_version property in gradle.properties
sed -i "s/mod_version=.*/mod_version=$new_version/" gradle.properties

#  Add the bumped gradle.properties to the last commit ammending the files to the last commit
git add gradle.properties
git commit --amend --no-edit